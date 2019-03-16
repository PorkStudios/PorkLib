#!/bin/bash

echo "Starting gource visualization..."
gource --key --hide dirnames,filenames,progress --max-file-lag 2 -a 1.8 -s 0.65
echo "Done!"
