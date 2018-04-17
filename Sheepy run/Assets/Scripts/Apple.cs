using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Apple : MonoBehaviour {



	// Use this for initialization
	void Start () {
	}

	// Update is called once per frame
	void Update () {

	}

	private void OnTriggerEnter2D (Collider2D other){
		if (other.GetComponent<Sheep> () != null) {
			GameControl.instance.takenApple ();
			transform.position = new Vector2 (-20f, -20f);
			TurboBar.turbo += 1f;
		}
	}

}
