#!/bin/bash
rm -rf gen
mkdir -p gen
cp *NaN.png gen/
for image in *.png; do
    if [[ $image == *NaN* ]]; then
        continue;
    fi
    # convert $image -virtual-pixel transparent -distort SRT -45 $image
    # continue;
    for i in $(seq -180 5 175); do
        if [ $i -eq 0 ]; then
            cp $image gen/${image%.*}0.png
            cp $image gen/${image%.*}360.png
            continue;
        fi
        if [ $i -lt 0 ]; then
            filename=${image%.*}$((360 + i)).png;
        else
            filename=${image%.*}$i.png;
        fi
        convert $image -virtual-pixel transparent -distort SRT $i gen/$filename
    done
done
pngquant --quality=70 --ext .png -f gen/*
