using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ApplePool : MonoBehaviour {

	public GameObject applePrefab;
	public int PoolSize = 8;
	public float spawnRate = 1f;
	public float spawnMin = 1f;
	public float spawnMax = 4f;
	public float spawnX = 7f;
	public float spawnYmin = -0.34f;
	public float spawnYmax = 2f;


	private GameObject[] apples;
	private Vector2 PoolPosition = new Vector2 (-15f, -25f);
	private float timeSinceSpawn;
	private int current = 0;

	// Use this for initialization
	void Start () {
		apples = new GameObject[PoolSize];
		for (int i = 0; i < PoolSize; i++) {
			apples [i] = (GameObject)Instantiate (applePrefab, PoolPosition, Quaternion.identity);
		}
	}

	// Update is called once per frame
	void Update () {
		timeSinceSpawn += Time.deltaTime;
		if (!GameControl.instance.gameOver && timeSinceSpawn >= spawnRate) {
			timeSinceSpawn = 0;
			spawnRate = Random.Range (spawnMin, spawnMax);
			float spawnY = Random.Range (spawnYmin, spawnYmax);
			apples [current].transform.position = new Vector2 (spawnX, spawnY);
			current++;
			if (current >= PoolSize) {
				current = 0;
			}
		}
	}
}
