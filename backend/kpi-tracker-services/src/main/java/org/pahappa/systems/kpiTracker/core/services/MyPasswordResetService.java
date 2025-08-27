package org.pahappa.systems.kpiTracker.core.services;

import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.service.PasswordResetService;

public interface MyPasswordResetService extends PasswordResetService {
    @Override
    void requestPasswordChange(String var1, String var2, String var3) throws ValidationFailedException;
}
