package com.visionbagel.payload;

import java.util.List;

public class SchemaInput {
    /**
     * The URL of the image to generate an image from.
     */
    public String image_url;

    public List<String> input_image_urls;

    /**
     * The prompt to generate an image from.
     */
    public String prompt;

    /**
     * The strength of the initial image. Higher strength values are better for this model. Default value: 0.95
     */
    public int strength;

    /**
     * The size of the generated image. Default value: landscape_4_3
     */
    public String image_size;

    /**
     * The ratio of the generated image. Default value: 16:9
     */
    public String aspect_ratio;

    /**
     * The number of inference steps to perform. Default value: 40
     */
    public int num_inference_steps;

    /**
     * The same seed and the same prompt given to the same version of the model will output the same image every time.
     */
    public int seed;

    /**
     * The same seed and the same prompt given to the same version of the model will output the same image every time.
     */
    public double guidance_scale;

    /**
     * If set to true, the function will wait for the image to be generated and uploaded before returning the response. This will increase the latency of the function but it allows you to get the image directly in the response without going through the CDN.
     */
    public boolean sync_mode;

    /**
     * The number of images to generate. Default value: 1
     */
    public int num_images;

    /**
     * If set to true, the safety checker will be enabled. Default value: true
     */
    public boolean enable_safety_checker;

    public String output_format = "png";
}
