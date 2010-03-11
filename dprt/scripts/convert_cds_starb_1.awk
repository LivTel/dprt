#Used by the WCS script to convert decimal ra,dec to hh mm ss dd mm ss
{rah = int($1/15)}
{ram = int(60*(($1/15)-rah)) }
{ras = 3600*( ($1/15)-(rah+(ram/60)) ) }

{ddeg = int($2)}
{dmin = int(60*($2-ddeg)) }
{dsec = 3600*( $2-(ddeg+(dmin/60)) ) }

{printf("%d:%d:%f %d:%d:%f %f\n",rah,ram,ras,ddeg,dmin,dsec,$3)} 
