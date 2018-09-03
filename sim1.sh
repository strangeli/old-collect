 #!/bin/sh
# Shell script for parameter variation in the microgrid model 
# to be improved: sed should not refer to line number in .c files but look for the  actual value or read out the line number first.
# strangeli


cd ./qualidaesc/examples

# changing the filename
sed "43s/.*/char filename[]= \"..\/..\/SIM_MA\/case1\/\";/" <microgrid_DAE.c >microgrid_DAE2.c	# to be improved
sed "45s/.*/char filename1[]= \"..\/..\/SIM_MA\/case1\/\";/" <microgrid_ODE.c >microgrid_ODE2.c	# to be improved

# delete old file and rename new file to old name because they need to have the same name always for qualidaes to work
rm microgrid_DAE.c
mv microgrid_DAE2.c microgrid_DAE.c

rm microgrid_ODE.c
mv microgrid_ODE2.c microgrid_ODE.c


# parameter variations stored in the array p
p=(1-1-12-96-48-48 1-1-12-48-48-48)

# loop for parameter variations
max=$((${#p[@]} - 1)) # length of loop - 1 = size of parameter array
for i in `seq 0 $max`
do

echo "index" $i
echo "p =" ${p[$i]}

sed "20s/.*/const char p[] = \"${p[$i]}\";/" <microgrid_DAE.c >microgrid_DAE2.c	# to be improved
sed "20s/.*/const char p[] = \"${p[$i]}\";/" <microgrid_ODE.c >microgrid_ODE2.c	# to be improved


rm microgrid_DAE.c
mv microgrid_DAE2.c microgrid_DAE.c

rm microgrid_ODE.c
mv microgrid_ODE2.c microgrid_ODE.c


# using case esac -- NEEDS TO BE ADAPTED MANUALLY!!!
case $i in
0) sed '24s/.*/const double Pgl2 = -96;/' <microgrid_DAE.c >microgrid_DAE2.c	# to be improved
   sed '24s/.*/const double Pgl2 = -96;/' <microgrid_ODE.c >microgrid_ODE2.c	# to be improved
   echo "nothing changed" 
   ;;
1) sed '24s/.*/const double Pgl2 = -48;/' <microgrid_DAE.c >microgrid_DAE2.c	# to be improved
   sed '24s/.*/const double Pgl2 = -48;/' <microgrid_ODE.c >microgrid_ODE2.c	# to be improved
   echo "Pgl2 set to -48"
   ;;
esac


rm microgrid_DAE.c
mv microgrid_DAE2.c microgrid_DAE.c

rm microgrid_ODE.c
mv microgrid_ODE2.c microgrid_ODE.c

# lines of other parameters to be changed in microgrid_DAE.c and microgrid.ODE.c
# NOTE THAT THE LINE NUMBERS should not change within these files!
# 20 p
#const char p[] = "1-1-12-96-48-48";

# 21 kdroop1, 22 kdroop2, 23 Pgl, 24 Pgl2, 25 vgnref1, 26 vgnref2
#const double kdroop1 = 1; 
#const double kdroop2 = 1;
#const double Pgl = -12;
#const double Pgl2 = -96;
#const double vgnref1 = 48;
#const double vgnref2 = 48;

# 34 lc1, 35 lc2, 36 lc3, 37 rc1, 38 rc2, 39 rc3, 40 ccap1, 41 ccap2, 42 ccap3
#const double lc1 = 0.237e-4 ; 
#const double lc2 = 0.237e-4 ; 
#const double lc3 = 0.237e-4 ; 
#const double rc1 = 0.152e-1 ; 
#const double rc2 = 0.152e-1 ; 
#const double rc3 = 0.152e-1 ; 
#const double ccap1 = 0.01 ; 
#const double ccap2 = 0.01 ; 
#const double ccap3 = 0.01 ; 

#  QldaesSetEps(qs,1e-10); # line 63 - tolerance
#  QldaesSetNOut(qs,2000); # line 64 - intervals
#  QldaesSetIniTime(qs,0.0); # line 65 - t0
#  QldaesSetEndTime(qs,2.0); # line 66 - tf


#  cond[0] = x[ic1]; #151
#  cond[1] = x[vgn1] - 48; #152
#  cond[2] = x[vgn2] - 48; #153
#  cond[3] = x[vgn3] - 48; #154
#  cond[4] = x[ic2] - x[ic3]; #154

#  double tstep = 1; ODE 158, DAE 169

#microgrid_DAE.c

#microgrid_ODE.c

cd ../build

mkdir ../../SIM_MA/case1/${p[$i]}

make
echo "make completed"

# run DAE simulation
./microgrid_dae
echo "dae completed"

# run ODE simulation
 ./microgrid_ode
 echo "ode completed"

cd ../../SIM_MA/case1/${p[$i]}

#sed '1s/.*/ /' <microgrid_output_DAE.txt >microgrid_output_DAE2.txt
#sed '1s/.*/ /' <microgrid_output_ODE.txt >microgrid_output_ODE2.txt

    read -p "Do you want to continue with plotting? [y/n] " yn
    case $yn in
        [Yy]* ) echo "ok"

echo "starting to plot."

# gnuplot -e "p='$p'" general_script.gp

name=(ic1 ic2 ic3 vgn1 vgn2 vgn3 vc1 vc2 vc3 icap1 icap2 icap3 igh1 igh2 igh3)

# loop for plotting in gnuplot
for var in `seq 0 5`
do

gnuplot -p << EOF

#! /usr/bin/gnuplot
# set terminal epslatex size 5,3 color colortext
set terminal png enhanced
set output '${p[$i]}_plot_${var}.png'

set datafile separator ","

set grid xtics ytics

set title "Comparison of the ODE and the DAE formulation"
set xlabel "time in seconds"

if ($var < 3) set ylabel "current in ampere"; else set ylabel "voltage in volt"
set key inside left

# plotting
plot "microgrid_output_DAE.txt" using 1:${var}+2 title '${name[$var]}_{DAE}' with lines, \
     "microgrid_output_ODE.txt" using 1:${var}+2 title '${name[$var]}_{ODE}' with lines



pause -1 "Hit any key to continue";

quit
EOF
done # var 0-5

for var_DAE in `seq 6 14`
do
gnuplot -p << EOF

#! /usr/bin/gnuplot
# set terminal epslatex size 5,3 color colortext
set terminal png enhanced
set output '${p[$i]}_plot__${var_DAE}.png'

set title "Algebraic states of the DAE solution"
set xlabel "time in seconds"

if ($var_DAE > 8) set ylabel "current in ampere"; else set ylabel "voltage in volt"
set key inside left

set datafile separator ","
set grid xtics ytics

plot "microgrid_output_DAE.txt" using 1:${var_DAE}+2 title '${name[$var_DAE]}_{DAE}' with lines


quit
EOF
done #varDAE

;; #;; from case

        [Nn]* ) echo "good bye" | exit;;
        * ) echo "Please answer yes or no.";;
    esac

cd ../../../qualidaesc/examples

done # for i




