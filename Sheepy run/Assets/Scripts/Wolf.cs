using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Wolf : MonoBehaviour {

	Collider2D col;

	// Use this for initialization
	void Start () {
		collider = GetComponent<PolygonCollider2D> ();
	}
	
	// Update is called once per frame
	void Update () {
		if (GameControl.instance.turboON) {
			col.isTrigger = true;
		} else {
			col.isTrigger = false;
		}
	}
}
