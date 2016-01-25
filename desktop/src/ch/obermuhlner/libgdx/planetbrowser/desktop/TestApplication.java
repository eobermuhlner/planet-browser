package ch.obermuhlner.libgdx.planetbrowser.desktop;

import static ch.obermuhlner.libgdx.planetbrowser.util.MathUtil.*;

public class TestApplication {

	public static void main(String[] args) {
		testCrater();
	}

	private static float craterSimpleRound(float distance) {
		float rimRadius = 0.7f;
		float rimHeight = 0.0f;

		float v = distance / rimRadius;
	    float rim = v * v - 1.0f;
	    
		float fadeOut = mix(
				rim, 
				rimHeight,
				smoothstep(rimRadius, 1.0f, distance));
		
		return fadeOut;
	}

	private static float craterSimpleFlat(float distance) {
		float flatRadius = 0.3f;
		float rimRadius = 0.7f;
		float flatHeight = -0.8f;
		float rimHeight = 0.1f;
		
	    float rim = mix(
		    	flatHeight,
				rimHeight,
				smoothstep(flatRadius, rimRadius, distance));
	    
		float fadeOut = mix(
				rim, 
				0.0f,
				smoothstep(rimRadius, 1.0f, distance));
		
		return fadeOut;
	}

	private static float craterComplexFlat(float distance) {
		float centralRadius = 0.1f;
		float flatRadius = 0.3f;
		float rimRadius = 0.7f;
		float centralHeight = -0.3f;
		float flatHeight = -0.6f;
		float rimHeight = 0.05f;
		
	    float central = mix(
		    	centralHeight,
		    	flatHeight,
				smoothstep(0.0f, centralRadius, distance));
	    
	    float inner = mix(
		    	central,
				rimHeight,
				smoothstep(flatRadius, rimRadius, distance));
	    
		float fadeOut = mix(
				inner, 
				0.0f,
				smoothstep(rimRadius, 1.0f, distance));
		
		return fadeOut;
	}

	private static float craterComplexStep(float distance) {
		float centralRadius = 0.06f;
		float flatRadius = 0.5f;
		float rimRadius = 0.7f;
		float centralHeight = -0.2f;
		float flatHeight = -0.4f;
		float rimHeight = 0.05f;
		int stepCount = 4;
		float stepRadiusDelta = (rimRadius - flatRadius) / stepCount;
		float stepHeightDelta = (rimHeight - flatHeight) / stepCount;
		float stepSmooth = 0.03f;
		float stepRadius = rimRadius - stepCount * stepRadiusDelta;
		float stepHeight = rimHeight - stepCount * stepHeightDelta;
		
	    float central = mix(
		    	centralHeight,
		    	stepHeight,
				smoothstep(0.0f, centralRadius, distance));
	    
	    stepRadius += stepRadiusDelta;
	    stepHeight += stepHeightDelta;
	    float step1 = mix(
		    	central,
				stepHeight,
				smoothstep(stepRadius, stepRadius + stepSmooth, distance));
	    
	    stepRadius += stepRadiusDelta;
	    stepHeight += stepHeightDelta;
	    float step2 = mix(
		    	step1,
				stepHeight,
				smoothstep(stepRadius, stepRadius + stepSmooth, distance));

	    stepRadius += stepRadiusDelta;
	    stepHeight += stepHeightDelta;
	    float step3 = mix(
		    	step2,
				stepHeight,
				smoothstep(stepRadius, stepRadius + stepSmooth, distance));
	    
	    stepRadius += stepRadiusDelta;
	    stepHeight += stepHeightDelta;
	    float step4 = mix(
		    	step3,
				stepHeight,
				smoothstep(stepRadius, stepRadius + stepSmooth, distance));
	    
		float fadeOut = mix(
				step4,
				0.0f,
				smoothstep(rimRadius, 1.0f, distance));
		
		return fadeOut;
	}

	private static void testCrater() {
		float x = 0.0f;
		while (x < 1.0f) {
//			float y = craterSimpleRound(x);
//			float y = craterSimpleFlat(x);
//			float y = craterComplexFlat(x);
			float y = craterComplexStep(x);
			System.out.println(y);
			
			x += 0.01f;
		}
	}
}
