clear all
javaaddpath ('C:\Dokumente und Einstellungen\strenge\Eigene Dateien\workspace\Test\bin')
javaaddpath ('C:\Dokumente und Einstellungen\strenge\Eigene Dateien\db-derby-10.7.1.1-bin\lib\derby.jar')
javaaddpath ('C:\Dokumente und Einstellungen\strenge\Eigene Dateien\db-derby-10.7.1.1-bin\lib\derbytools.jar')


wert2 = Dreiphasig(3);
%Dreiphasig.main('embedded');
wert2.connection('C:\Dokumente und Einstellungen\strenge\Eigene Dateien\GridVisDB3Phasenalt');
wert2.read();
wert2.shutdown();
daten = wert2.mess;
plot(daten(:,1)/1000000000/(24*3600)+datenum('Jan-00-1970'),daten(:,4));
datetick('x',0);
