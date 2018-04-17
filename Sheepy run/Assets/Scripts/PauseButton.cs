using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PauseButton : MonoBehaviour {

	public Sprite playSprite;
	public Sprite pauseSprite;

	Image myImageComponent;

	private Image img;

	// Use this for initialization
	void Start () {
		myImageComponent = GetComponent<Image>();
	}
	
	// Update is called once per frame
	void Update () {
		if (GameControl.instance.gamePaused) {
			myImageComponent.sprite = playSprite;
		} else {
			myImageComponent.sprite = pauseSprite;
		}
	}

	
}
