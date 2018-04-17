using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class GameControl : MonoBehaviour {

	public static GameControl instance;
	public GameObject gameOverText;
	public bool gameOver = false;
	public bool gamePaused = false;
	public float scrollSpeed = -1.5f;
	public Text scoretext;
	public Text highScoreText;
	public int highscore;
	public int scoreRate = 5;
	public GameObject pauseText;
	public GameObject introLogo;
	public GameObject sheepy;
	public int maxApples = 30;
	public bool turboON = false;

	public int apples = 0;
	private int score = 0;


	// Use this for initialization
	void Awake () {
		if (instance == null) {
			instance = this;
		} else if (instance != this) {
			Destroy (gameObject);
		}
		InvokeRepeating ("Score", 0f, 0.1f);
		

	}

	void Start(){
		introLogo.SetActive (true);
		sheepy.SetActive (true);
		highscore = PlayerPrefs.GetInt ("HighScore", 0);
	}


		
		
	
	// Update is called once per frame
	void Update () {
		if (gameOver == true && (Input.GetMouseButtonDown(0) || Input.GetKeyDown("space"))) {
			SceneManager.LoadScene (SceneManager.GetActiveScene ().buildIndex);

		}
		if (Time.realtimeSinceStartup > scoreRate) {
			introLogo.SetActive (false);
			sheepy.SetActive (false);
		}
		if (gamePaused) {
			Time.timeScale = 0;
			pauseText.SetActive (true);
		} else {
			pauseText.SetActive (false);
			Time.timeScale = 1;
		}

		if (turboON) {
			Time.timeScale = 2;
		} else {
			Time.timeScale = 1;
		}

	}

	public void Score(){
		if (!gamePaused && !gameOver) {
			score++;
			scoretext.text = "DISTANCE : " + score.ToString ();
		}
		if (score > highscore) {
			highscore = score;
			PlayerPrefs.SetInt ("HighScore", highscore);
		}
	}

	public void Died(){
		highScoreText.text = "BEST SCORE: " + highscore;
		gameOverText.SetActive (true);
		gameOver = true;
	}

	public void Pause(){
		gamePaused = !gamePaused;
	}

	public void takenApple(){
		if (apples < maxApples) {
			apples++;
		}
	}

	IEnumerator turboMode(){
		turboON = true;
		yield return new WaitForSecondsRealtime (5f);
		turboON = false;
	}
}
