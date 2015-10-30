package org.biobank.platedecoder.model;

public abstract class ImageSource {

    public enum ImageSourceType {

        FILE_SYSTEM("file system"),
        FLATBED_SCANNER("flatbed scanner");

        private final String description;

        private ImageSourceType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private final ImageSourceType type;

	private final String imageFileUrl;

	public ImageSource(ImageSourceType type, String imageFileUrl) {
		this.type = type;
		this.imageFileUrl = imageFileUrl;
	}

	/**
	 * @return the type
	 */
	public ImageSourceType getType() {
		return type;
	}

	/**
	 * @return the imageFileUrl
	 */
	public String getImageFileUrl() {
		return imageFileUrl;
	}

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(type);

        if (type == ImageSourceType.FILE_SYSTEM) {
            buf.append(": ");
            buf.append(imageFileUrl);
        }

        return buf.toString();
    }

}
