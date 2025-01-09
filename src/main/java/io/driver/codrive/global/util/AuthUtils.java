package io.driver.codrive.global.util;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.driver.codrive.global.entity.OwnedEntity;
import io.driver.codrive.global.exception.ForbiddenApplicationException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtils {
	public Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		return Long.valueOf(authentication.getPrincipal().toString());
	}

    private boolean isOwnedEntity(OwnedEntity entity) {
        return Objects.equals(entity.getOwnerId(), getCurrentUserId());
    }

    public void checkOwnedEntity(OwnedEntity entity) {
        if (!isOwnedEntity(entity)) {
            throw new ForbiddenApplicationException("해당 리소스에 대한 권한이 없습니다.");
        }
    }
}
