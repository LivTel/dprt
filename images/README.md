# DpRt : images

This directory contains a Dockerfile for building a DpRt docker container. Rather than deploying the software as a series of jar's in a tarball, we create a docker container and export that to the control computer.

## Deployment

### DpRt docker

Currently the DpRt docker is only available for the LOCI data pipeline (which is being used to evaluate NRT technologies). The DpRt docker is instrument specific due to the config file, and libdprt shared library instance that is invoked.

To build a docker container do the following (on an LT development machine, where the DpRt software repository is installed at /home/dev/src/dprt) :

* **cd ~dev/src/dprt/images** (i.e. this directory)
* **sudo docker build -f loci_dprt_java -t loci_dprt_java_image /** Build the docker container from the **loci_dprt_java** file.
* **docker save -o loci_dprt_java_image.tar loci_dprt_java_image** Save the constructed docker container into the **loci_dprt_java_image.tar** tarball.

This saved docker tarball can then be copied to the control computer using scp.

The docker can then be installed / loaded into the local system as follows:

* ssh into machine
* **cd images**
* **sudo docker load -i loci_dprt_java_image.tar**

You now need to install the Loci DpRt config files before starting the docker.


### DpRt config files

To generate a tarball containing the Loci DpRt config files, on an LT development machine , where the dprt software repository is installed at /home/dev/src/dprt, do the following:

* **cd ~dev/src/dprt/scripts/**
* **./dprt_create_config_tarball loci1** This currently uses the docker.<config file> version of the config files.

The config tarball ends up at: ~dev/public_html/dprt/deployment/dprt_config_deployment_loci1.tar.gz . Copy the config tarball to loci1:

* **scp -C ~dev/public_html/dprt/deployment/dprt_config_deployment_loci1.tar.gz admin@loci1:images**

Then install the config tarball as follows:

* **ssh admin@loci1**
* **cd /**
* **sudo tar xvfz /home/admin/images/dprt_config_deployment_loci1.tar.gz** 
* **sudo chown root:root /** This fixes root's permissions.

### Starting the Loci DpRt

The Loci DpRt can then be started as follows:

* **sudo docker run -p 6880:6880 --mount type=bind,src=/icc,dst=/icc --mount type=bind,src=/data,dst=/data --name=loci-dprt -it -d --restart unless-stopped loci_dprt_java_image**

For this to work the Loci DpRt config files need to have been installed under **/icc** first. 

An explanation of the command line:

* **-p 6880:6880** : allow access via port 6880
* **--mount type=bind,src=/icc,dst=/icc** : allow docker to access /icc as /icc to load java layer config files, and write logs
* **--mount type=bind,src=/data,dst=/data** : allow docker to access /data as /data to access FITS images written by the Loci CCD Flask API. 
* **--name=loci-dprt** docker is called loci-dprt in docker ps
* **-d** : docker is a daemon (detach from terminal)
* **--restart unless-stopped** : restart docker on exit, unless it has been explicitly stopped using docker kill
* **-it** : -t allocate a pseodo-tty, -i interactive. Do we need these?

### Stopping the Loci DpRt

The Loci DpRt can be stopped as follows:

* **sudo docker ps**
Find the **loci-dprt** container id and then do the following:

* **sudo docker kill &lt;containerid&gt;**
* **sudo docker remove &lt;containerid&gt;**

You need to remove the container to re-use the loci-dprt container name.
