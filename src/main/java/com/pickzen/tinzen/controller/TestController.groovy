package com.pickzen.tinzen.controller

import com.pickzen.api.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(value="test")
class TestController extends BaseController {
	@RequestMapping("/foo")	
	@ResponseBody
	def foo() {
		return [foo:"foo"]
	}
}
