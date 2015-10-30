package org.biobank.platedecoder.model;

public class ImageSourceFileSystem extends ImageSource {

    public ImageSourceFileSystem(String imageFileUrl) {
        super(ImageSource.ImageSourceType.FILE_SYSTEM, imageFileUrl);
    }

}
