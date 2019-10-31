#!/bin/bash

for image in *.png; do 
    for i in $(seq -175 5 175); do 
        if [ $i -eq 0 ]; then
            continue;
        fi
        if [ $i -lt 0 ]; then
            filename=${image%.*}.$((360 + i)).png;
        else
            filename=${image%.*}.$i.png;
        fi
        convert $image -virtual-pixel transparent -distort ScaleRotateTranslate $i $filename;
    done
done
