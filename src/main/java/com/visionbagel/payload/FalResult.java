package com.visionbagel.payload;

import ai.fal.client.Output;
import com.visionbagel.entitys.FalImage;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FalResult {
    public ArrayList<Map<String, ?>> images = new ArrayList<>();

    public String requestId;
    public String seed;
    public String prompt;

    public FalResult(Output<JsonObject> r, List<FalImage> images) {
        var data = r.getData();
        this.prompt = data.get("prompt").toString();
        this.seed = data.get("seed").toString();
        this.requestId = r.getRequestId();

        images.forEach(image -> this.images.add(Map.of(
            "width", image.width,
            "height", image.height,
            "url", image.url,
            "content_type", image.content_type
        )));
    }
}
