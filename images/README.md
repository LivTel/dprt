# DpRt : images

This directory contains a Dockerfile for building a DpRt docker container. Rather than deploying the software as a series of jar's in a tarball, we create a docker container and export that to the control computer.

TODO : wcs_fit deployment.

## Deployment

### DpRt docker

Currently the DpRt docker is only available for the LOCI data pipeline (which is being used to evaluate NRT technologies). The DpRt docker is instrument specific due to the config file, and libdprt shared library instance that is invoked.

To build a docker container do the following (on an LT development machine, where the DpRt software repository is installed at /home/dev/src/dprt) :

* **cd ~dev/src/dprt/images** (i.e. this directory)
* **./provision_dprt** Run the provisioning script, which copies the Java libraries from /home/dev/bin/javalib, the DpRt class files from /home/dev/bin/dprt/java/ngat/dprt, and the C libraries from /home/dev/bin/lib/x86_64-linux/, into a created **docker** directory tree (created in the images directory). This allows us to use a local context for the docker build.
* **docker build -f loci_dprt_java -t loci_dprt_java_image .** Build the docker container from the **loci_dprt_java** file.
* **docker save -o loci_dprt_java_image.tar loci_dprt_java_image** Save the constructed docker container into the **loci_dprt_java_image.tar** tarball.

This saved docker tarball can then be copied to the control computer using scp.

The docker can then be installed / loaded into the local system as follows:

* ssh into machine
* **cd images**
* **docker load -i loci_dprt_java_image.tar**

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

* **docker run -p 6880:6880 --mount type=bind,src=/icc,dst=/icc --mount type=bind,src=/data,dst=/data --name=loci-dprt -it -d --restart unless-stopped loci_dprt_java_image**

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

### Command line test programs

The DpRt usually ships with a suite of command line test programs. A few of these are now included in the DpRt image and can be invoked, using a separate instance of the loci_dprt_java_image container with an alternate command line (and entry-point).

#### SendDpRtCommand

* **ssh user@loci1**
* **docker run --mount type=bind,src=/icc,dst=/icc --mount type=bind,src=/data,dst=/data --entrypoint /bin/bash -it loci_dprt_java_image**
* Inside the created container shell, type the following:
* **cd /docker/bin/dprt/test**
* **export CLASSPATH="${CLASSPATH}:."**
* Help command: **java SendDpRtCommand -help**
* Example data reduction: **java SendDpRtCommand -ip 192.168.1.28 -dprtport 6880 -expose /data/k_e_20251027_7_1_1_0.fits**

Note the FITS image to reduce has to be in /data, to access the directory the FITS image data is in (as we are running SendDpRtCommand inside a docker). Both SendDpRtCommand and the DpRt itself will have to access the data.

#### dprt_test

'dprt_test' is a C layer program that can be used to invoke the core libdprt data reduction routines on FITS images.
For example, to reduce an exposure:

* **ssh admin@loci1**
* **sudo docker run --mount type=bind,src=/icc,dst=/icc --mount type=bind,src=/data,dst=/data --entrypoint /bin/bash -it loci_dprt_java_image**
* Inside the created container shell, type the following:
* **cd /docker/bin/libdprt/test/x86_64-linux/**
* **/docker/bin/libdprt/test/x86_64-linux/dprt_test -e /data/k_e_20250127_12_1_1_0.fits**

This can all be done as a single command as follows:

* **ssh admin@loci1**
* **sudo docker run --mount type=bind,src=/icc,dst=/icc --mount type=bind,src=/data,dst=/data --entrypoint /bin/bash -it loci_dprt_java_image -c "cd /docker/bin/libdprt/test/x86_64-linux/ ; /docker/bin/libdprt/test/x86_64-linux/dprt_test -e /data/k_e_20250127_12_1_1_0.fits"**

We need the /icc mount to access the soft-linked dprt.properties, and the /data mount to access the data.
We have to run dprt_test from it's own directory as it tries to load './dprt.properties' for configuration, that is now in /docker/bin/libdprt/test/x86_64-linux/ as a soft-link to the (external to docker) /icc config file. Note everything after and including the '-c' is actually command line arguments to the entrypoint (/bin/bash in the above case).

