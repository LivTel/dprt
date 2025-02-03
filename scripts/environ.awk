#
#
#!/bin/awk -f
# test envionment handling
BEGIN {
	for(env in ENVIRON)
		print env "=" ENVIRON[env]
}
