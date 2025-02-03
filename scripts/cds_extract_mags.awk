#Tries to be clever and combine magnitudes. If there is anan astrometric shift
# between r1 and r2, there is a rist that both objects will appear in the calibration
# catalogure. There is another version of this script which uses just the r2 catalogue
# and ignore the r1 altogether. It may well be more robust
{ delr = ($4-$5) ; diffr = sqrt(delr*delr) }
#{ delb = ($2-$3) ; diffb = sqrt(delb*delb) }
# Ouch. That's no way to average magnitudes!
( $4!="---" && $5!="---" ) { print $1,($4+$5)/2 }
( $4=="---" ) {print  $1,$5 }
( $5=="---" ) {print  $1,$4 }
