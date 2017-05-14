package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;


@With(Secure.class)
@Check("administrator")
public class Application extends Controller {

	public static void index() {
		String user = Security.connected();
		render(user);
		//render();
	}

	public static void about() {
		render();
	}

	public static void contact() {
		render();
	}

}