package io.driver.codrive.modules.follow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.driver.codrive.modules.follow.service.FollowService;
import io.driver.codrive.modules.global.constants.APIConstants;
import io.driver.codrive.modules.global.model.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APIConstants.API_PREFIX + "/follow")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;

	@PostMapping("/{nickname}")
	public ResponseEntity<BaseResponse<Void>> follow(@PathVariable(name = "nickname") String nickname) {
		followService.follow(nickname);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

	@DeleteMapping("/{nickname}")
	public ResponseEntity<BaseResponse<Void>> cancelFollow(@PathVariable(name = "nickname") String nickname) {
		followService.cancelFollow(nickname);
		return ResponseEntity.ok(BaseResponse.of(null));
	}

}
