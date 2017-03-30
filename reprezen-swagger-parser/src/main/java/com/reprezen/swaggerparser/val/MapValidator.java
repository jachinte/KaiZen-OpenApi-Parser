package com.reprezen.swaggerparser.val;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.reprezen.swaggerparser.jsonoverlay.JsonOverlay;
import com.reprezen.swaggerparser.jsonoverlay.coll.MapOverlay;

public class MapValidator<T extends JsonOverlay<?>> extends OverlayValidator<MapOverlay<T>> {

    private Validator<T> elementValidator;

    public MapValidator(Validator<T> elementValidator) {
        this.elementValidator = elementValidator;
    }

    @Override
    public void validate(MapOverlay<T> overlay, ValidationResults results) {
        super.validate(overlay, results, ObjectNode.class);
        for (T value : overlay.getStore().getOverlays()) {
            elementValidator.validate(value, results, value.getKey());
        }
    }
}
