#!/bin/awk -f
# input from stdin or a file.
# call as :
# print_line.awk -v LINE=1 <input file>
# to print line 1 of the file
# $Header: /space/home/eng/cjm/cvs/dprt/scripts/rd.awk,v 1.1 2010-01-21 10:44:53 cjm Exp $
# Version : $Revision: 1.1 $
/^[0-9]/ {
#     1767-0001964              014.922917        
	 {
	   if($20 != "|")
	     {
	       sig = 0; 
	       split($2,co,"+");
#printf " col + %lf %lf\n",co[1],co[2];
	       if(co[2] == 0.0) 
		 {
		   split($2,co,"-");
		   sig = -1.0;
		 }
	       rahh = co[1]/15.;
	       ramm = ((co[1] % 15)/15.)*60.;
	       rass = (((co[1] % 15)/15.)*3600.)%60;
	       dechh = co[2];
	       decmm = (co[2] % 1)*60.;
	       decss = (decmm % 1)*60.;
	       
	       if(!sig)
		 {
		   if((rass < 10) && (decss < 10))
		     printf "%02d:%02d:0%2.4lf +%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;	     
		   else if(rass < 10)
		     printf "%02d:%02d:0%2.4lf +%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else if(decss < 10)
		     printf "%02d:%02d:%2.4lf +%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else
		     printf "%02d:%02d:%2.4lf +%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		 }
	       else
		 {
		   if((rass < 10) && (decss < 10))
		     printf "%02d:%02d:0%2.4lf -%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;	     
		   else if(rass < 10)
		     printf "%02d:%02d:0%2.4lf -%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else if(decss < 10)
		     printf "%02d:%02d:%2.4lf -%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else
		     printf "%02d:%02d:%2.4lf -%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		 }
	       
	       printf "%8.1lf", $6;
	       printf "%8.1lf", $7;
	       printf "%8.3lf", $15;
	       printf "%8.3lf", $20;
	       printf "%8.3lf", $25;
	       printf "%8.3lf", $30;
	       printf "%8.3lf", $35;	   
	       printf "\n";
	     }
	   else
	     {
#printf("Tyc ");
	       sig = 0; 
	       split($3,co,"+");
#printf " col + %lf %lf\n",co[1],co[2];
	       if(co[2] == 0.0) 
		 {
		   split($3,co,"-");
		   sig = -1.0;
		 }
	       rahh = co[1]/15.;
	       ramm = ((co[1] % 15)/15.)*60.;
	       rass = (((co[1] % 15)/15.)*3600.)%60;
	       dechh = co[2];
	       decmm = (co[2] % 1)*60.;
	       decss = (decmm % 1)*60.;
	       
	       if(!sig)
		 {
		   if((rass < 10) && (decss < 10))
		     printf "%02d:%02d:0%2.4lf +%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;	     
		   else if(rass < 10)
		     printf "%02d:%02d:0%2.4lf +%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else if(decss < 10)
		     printf "%02d:%02d:%2.4lf +%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else
		     printf "%02d:%02d:%2.4lf +%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		 }
	       else
		 {
		   if((rass < 10) && (decss < 10))
		     printf "%02d:%02d:0%2.4lf -%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;	     
		   else if(rass < 10)
		     printf "%02d:%02d:0%2.4lf -%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else if(decss < 10)
		     printf "%02d:%02d:%2.4lf -%02d:%02d:0%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		   else
		     printf "%02d:%02d:%2.4lf -%02d:%02d:%2.3lf",rahh,ramm,rass,dechh,decmm,decss;
		 }
	       
	       printf "%8.1lf", $7;
	       printf "%8.1lf", $8;
	       printf "%8.3lf", $16;
	       printf "%8.3lf", $21;
	       printf "%8.3lf", $26;
	       printf "%8.3lf", $31;
	       printf "%8.3lf", $36;	   
	       printf "\n"; 
	     }	   
	 }
}
#
# $Log: not supported by cvs2svn $
#
