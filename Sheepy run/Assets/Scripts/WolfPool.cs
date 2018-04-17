using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class WolfPool : MonoBehaviour {

	public GameObject wolfPrefab;
	public int wolfPoolSize = 5;
	public float spawnRate = 4f;
	public float spawnMin = 1f;
	public float spawnMax = 4f;
	public float spawnX = 7f;
	public float spawnY = -0.34f;


	private GameObject[] wolves;
	private Vector2 wolfPoolPosition = new Vector2 (-15f, -25f);
	private float timeSinceSpawn;
	private int currentWolf = 0;

	// Use this for initialization
	void Start () {
		wolves = new GameObject[wolfPoolSize];
		for (int i = 0; i < wolfPoolSize; i++) {
			wolves [i] = (GameObject)Instantiate (wolfPrefab, wolfPoolPosition, Quaternion.identity);
		}
	}
	
	// Update is called once per frame
	void Update () {
		timeSinceSpawn += Time.deltaTime;
		if (!GameControl.instance.gameOver && timeSinceSpawn >= spawnRate) {
			timeSinceSpawn = 0;
			spawnRate = Random.Range (spawnMin, spawnMax);
			wolves [currentWolf].transform.position = new Vector2 (spawnX, spawnY);
			currentWolf++;
			if (currentWolf >= wolfPoolSize) {
				currentWolf = 0;
			}
		}
	}
}
