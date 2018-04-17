using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class TurboBar : MonoBehaviour {

	public float maxTurbo;
	public static float turbo = 0f;
	private Image bar;

	// Use this for initialization
	void Start () {
		maxTurbo = (float)GameControl.instance.maxApples;
		bar = GetComponent<Image> ();
	}
	
	// Update is called once per frame
	void Update () {
		if (bar != null) {
			bar.fillAmount = turbo / GameControl.instance.maxApples;
		}
	}
}
