#!/bin/bash

for image in *.png; do 
    #for i in $(seq 0 5 130); do 
        convert $image -virtual-pixel transparent -distort ScaleRotateTranslate -45 $image;
    #done
done
