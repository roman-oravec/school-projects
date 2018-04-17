using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class Sheep : MonoBehaviour {

    public float upForce = 200f;
    public Collider2D sheepCollider;
    public Collider2D grassCollider;
    public Collider2D grass2Collider;
    public bool isAirborne;

    private bool isDead = false;
    private Rigidbody2D rb2d;
	private bool validInput; 
    

	// Use this for initialization
	void Start () {
        rb2d = GetComponent<Rigidbody2D>();
        sheepCollider = GetComponent<PolygonCollider2D>();
        grassCollider = GameObject.Find("grass").GetComponent<BoxCollider2D>();
        grass2Collider = GameObject.Find("grass2").GetComponent<BoxCollider2D>();

    }
	
	// Update is called once per frame
	void Update () {

        if (isDead == false)
        {
			validateInput ();
            if (sheepCollider.Distance(grassCollider).distance > 0.2f && sheepCollider.Distance(grass2Collider).distance > 0.3f)
            {
                isAirborne = true;
                
            }
            else
            {
                isAirborne = false;
            }
				

			if (Input.GetMouseButtonUp (0) && validInput && !isAirborne && !GameControl.instance.gamePaused) {
				//Put your code here
				rb2d.AddForce (new Vector2 (0, upForce), ForceMode2D.Impulse);
			}
			

			//MOBILE DEVICES
			else if (Input.touchCount > 0 && Input.GetTouch(0).phase == TouchPhase.Ended && validInput && !isAirborne && !GameControl.instance.gamePaused)
			{
				//Put your code here
				rb2d.AddForce(new Vector2(0, upForce), ForceMode2D.Impulse);
			}
        }
		
	}

	void OnCollisionEnter2D(Collision2D col){
		if (col.collider.GetComponent<Wolf>() != null) {
			isDead = true;
			GameControl.instance.Died ();
			rb2d.velocity = Vector2.zero;
			col.collider.enabled = false;
		}


	}

	void validateInput()
	{
		if (Input.GetMouseButtonDown(0))
		{
			if (EventSystem.current.IsPointerOverGameObject())
				validInput = false;
			else
				validInput = true;
		}


		if (Input.touchCount > 0 && Input.GetTouch(0).phase == TouchPhase.Began)
		{
		if (EventSystem.current.IsPointerOverGameObject(Input.GetTouch(0).fingerId))
		validInput = false;
		else
		validInput = true;
		}
	
	}
}
