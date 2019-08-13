# Deployment instructions

## Install all required software
cd tools/ubuntu-setup && ./all.sh

## Build
./gradlew distDocker

## Install Ansible
sudo apt-get install python-pip
sudo pip install ansible==2.5.2
sudo pip install jinja2==2.9.6

Set OPENWHISK_TMP_DIR variable in .bash_profile to preserve OpenWhisk configuration on reboot. Also export Path to wsk cli tool.
```
echo "export OPENWHISK_TMP_DIR=~/openwhisk-persistent" >> ~/.profile

echo "export PATH=$PATH::/home/master/openwhisk-singularity/bin" >> ~/.profile
```

## Log out and log back in for .profile changes to come into effect

## Commands below are to be executed in ansible directory

Run commands below for CouchDB configuration. Example configuration below
```
export OW_DB=CouchDB
export OW_DB_USERNAME=admin
export OW_DB_PASSWORD=password
export OW_DB_PROTOCOL=http
export OW_DB_HOST=172.17.0.1
export OW_DB_PORT=5984

ansible-playbook -i environments/local setup.yml
```

Run these commands to deploy Openwhisk
```
ansible-playbook -i environments/local couchdb.yml
ansible-playbook -i environments/local initdb.yml
ansible-playbook -i environments/local wipe.yml
ansible-playbook -i environments/local openwhisk.yml
```

Installs a catalog of public packages and action
```
ansible-playbook -i environments/local postdeploy.yml
```

To use the API gateway run these commands
```
ansible-playbook -i environments/local apigateway.yml
ansible-playbook -i environments/local routemgmt.yml
```

In main folder: setting wsk properties
```
cd <openwhisk-home>
./bin/wsk property set --auth `cat ansible/files/auth.guest`
./bin/wsk property set --apihost 172.17.0.1
```

Finally, a hello world

```
./bin/wsk action invoke /whisk.system/utils/echo -p message hello --insecure --result
```

Hot-swapping a Single component

```
cd <openwhisk-home>
./gradlew distDocker

cd ansible
ansible-playbook -i environments/local invoker.yml
```