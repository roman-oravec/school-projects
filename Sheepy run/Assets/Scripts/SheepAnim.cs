using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SheepAnim : MonoBehaviour {

    private Animator anim;

	// Use this for initialization
	void Start () {
        anim = GetComponent<Animator>();

    }
	
	// Update is called once per frame
	void Update () {
		if (GameControl.instance.gameOver) {
			anim.SetTrigger ("Dead");
		}
		if (Input.GetMouseButtonDown (0) && !GameControl.instance.gamePaused ) {
            
            anim.SetTrigger("Jump");
        }

        
	}
}
