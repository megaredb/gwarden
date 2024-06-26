package com.megared.gwarden.database.managers;

import com.megared.gwarden.database.NitriteManager;

class BasicDocumentManager {
    private final NitriteManager nitriteManager;

    public BasicDocumentManager() {
        this.nitriteManager = NitriteManager.getInstance();
    }

    protected NitriteManager getNitriteManager() {
        return nitriteManager;
    }
}
