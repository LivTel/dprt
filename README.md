# dprt


This directory contains the Real Time Data Pipeline.
This is the Java layer of the real time data pipeline, the C layer is in libdprt.

## Directory Structure


* **images** This contains a Dockerfile for building a Loci DpRt docker container. Rather than deploying the software as a series of jar's in a tarball, we create a docker container and export that to the control computer.
* **java** This contains the source code for the Liverpool Telescope Java robotic layer, which receives commands from the instrument as serialsed Java objects. The commands result in calls, via JNI, to software located in the [libdprt](https://github.com/LivTel/libdprt) repository.
* **latex** This contains LaTeX documentation pertaining to the data pipeline.
* **scripts** This directory contains scripts to make indepenent deployments of both the data-pipeline and wcs_fit scripts, the wcs_fit script and associated sub-scripts.
* **test** This directory contains the test program SendDpRtCommand, which can be used to manually send data-pipelining requests to the DpRt.

## Dependencies / Prerequisites

* The ngat repo/package must be installed: https://github.com/LivTel/ngat .  The specific sub-packages required are urrently:
  * ngat_util_logging.jar
  * ngat_util.jar
  * ngat_net.jar
  * ngat_fits.jar
  * ngat_message_inst_dp.jar
  * ngat_message_base.jar
* The software can only be built from within an LT development environment

## Deployment

The data pipeline is usually deployed along with the instrument control software, and the deployment instructions can usually be found within the instrument deployment scripts. This is true of [IO:O](https://github.com/LivTel/ioo/blob/master/scripts/o_make_deployment) and [Sprat](https://github.com/LivTel/sprat/blob/master/scripts/sprat_make_deployment) for instance.

The real time data pipeline has been deployed indepenently in the past on it's own data reduction machine. See [dprt_make_deployment](scripts/dprt_make_deployment) for this deployment mechanism.

For LOCI we are trying to dockerise the DpRt into a docker container. See the [images](images) directory for details.

At times the wcs_fit script and associated sub-scripts have been deployed separately, see  [wcs_fit_make_deployment](scripts/wcs_fit_make_deployment).

## Command line test tools

There is a command line test tool to test invoking the real time data pipeline. The software is in the [test](test) directory.
