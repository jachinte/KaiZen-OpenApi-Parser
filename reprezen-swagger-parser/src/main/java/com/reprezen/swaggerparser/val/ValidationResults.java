package com.reprezen.swaggerparser.val;

import static com.reprezen.swaggerparser.val.ValidationResults.Severity.ERROR;
import static com.reprezen.swaggerparser.val.ValidationResults.Severity.INFO;
import static com.reprezen.swaggerparser.val.ValidationResults.Severity.MAX_SEVERITY;
import static com.reprezen.swaggerparser.val.ValidationResults.Severity.NONE;
import static com.reprezen.swaggerparser.val.ValidationResults.Severity.WARNING;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.reprezen.swaggerparser.jsonoverlay.JsonOverlay;

public class ValidationResults {

    public enum Severity {
        NONE, INFO, WARNING, ERROR;

        public static final Severity MAX_SEVERITY = ERROR;

        public boolean lt(Severity other) {
            return this.compareTo(other) < 0;
        }

        public boolean le(Severity other) {
            return this.compareTo(other) <= 0;
        }

        public boolean gt(Severity other) {
            return this.compareTo(other) > 0;
        }

        public boolean ge(Severity other) {
            return this.compareTo(other) >= 0;
        }
    };

    List<ValidationItem> items = Lists.newArrayList();
    List<String> crumbs = Collections.emptyList();

    public void addInfo(String msg) {
        items.add(new ValidationItem(INFO, msg, crumbs));
    }

    public void addWarning(String msg) {
        items.add(new ValidationItem(WARNING, msg, crumbs));
    }

    public void addError(String msg) {
        items.add(new ValidationItem(ERROR, msg, crumbs));
    }

    public void add(ValidationResults results) {
        items.addAll(results.getItems());
    }

    public Collection<ValidationItem> getItems() {
        return items;
    }

    public Severity getSeverity() {
        Severity severity = NONE;
        for (ValidationItem item : items) {
            if (item.getSeverity().gt(severity)) {
                severity = item.getSeverity();
                if (severity == MAX_SEVERITY) {
                    break;
                }
            }
        }
        return severity;
    }

    public <T extends JsonOverlay<?>> void validateWithCrumb(String crumb, Validator<T> validator, T object) {
        List<String> priorCrumbs = crumbs;
        try {
            crumbs = Lists.newArrayList(priorCrumbs);
            crumbs.add(crumb);
            validator.validate(object, this);
        } finally {
            crumbs = priorCrumbs;
        }
    }

    public void withCrumb(String crumb, Runnable code) {
        List<String> priorCrumbs = crumbs;
        try {
            crumbs = Lists.newArrayList(priorCrumbs);
            crumbs.add(crumb);
            code.run();
        } finally {
            crumbs = priorCrumbs;
        }
    }

    public static class ValidationItem {
        private Severity severity;
        private String msg;
        private List<String> crumbs;

        public ValidationItem(Severity severity, String msg, List<String> crumbs) {
            super();
            this.severity = severity;
            this.msg = msg;
            this.crumbs = crumbs;
        }

        public Severity getSeverity() {
            return severity;
        }

        public String getMsg() {
            return msg;
        }

        public List<String> getCrumbs() {
            return crumbs;
        }

        @Override
        public String toString() {
            String label = crumbs != null & !crumbs.isEmpty() ? StringUtils.join(crumbs, '.') + ": " : "";
            return label + msg;
        }
    }
}
