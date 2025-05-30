import { Game, Scene } from "phaser";

import { TextButton } from "../utils/TextButton";

import axios from "axios";
import Swal from "sweetalert2";
import { API } from "../utils/Api";
import { Dungeon } from "../types/Types";
import { ErrorAlert, InfoAlert } from "../utils/Alerts";
import { getFromTileSet } from "../utils/Tileset";
import { playBackgroundMusic, setupBackgroundMusic } from "../utils/MusicManager";
import { generateSceneWideSymbols } from "../utils/SceneWideSymbols";

class MenuScene extends Scene {
  private bg: Phaser.GameObjects.Sprite;
  private title: Phaser.GameObjects.Text;
  private subtitle: Phaser.GameObjects.Text;
  private subtitle2: Phaser.GameObjects.Text;
  private newGame: TextButton;
  private exitGame: TextButton;
  private numbersList: any;

  constructor() {
    super("scene-menu");
  }

  init() {}

  preload() {
    // load required assets...
    this.load.baseURL = "/";
    // this.load.baseURL =
    //   window.location.protocol + "//" + window.location.host + "/";
    this.load.image(
      "menu-background",
      window.Config.skinFile.main_menu.background_image
    );

    if (typeof window.Config.skinFile.main_menu.background_music == "string") {
      this.load.audio(
        "main-menu-music-",
        window.Config.skinFile.main_menu.background_music
      );
    } else {
      for (var audio in window.Config.skinFile.main_menu.background_music) {
        // it's actually pretty cheap to cache the images here because of how tiny they are
        // (this may change in the future if we have perf issues).
        this.load.audio("main-menu-music-" + audio, [audio]);
      }
    }

    this.numbersList = [-50,-49,-48,-47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50];

    setupBackgroundMusic(
      this,
      "main-menu-",
      window.Config.skinFile.main_menu.background_music
    );
  }

  create() {
    generateSceneWideSymbols(this);
    
    playBackgroundMusic(
      "main-menu-",
      this,
      window.Config.skinFile.main_menu.background_music
    );

    this.bg = this.add.sprite(0, 0, "menu-background");

    // stretch image to fit, but ensure that scaling is done relatively in ratio!
    this.bg.setDisplaySize(
      this.renderer.width * (this.bg.width / this.bg.height),
      this.renderer.height * (this.bg.height / this.bg.width) + 100
    );
    this.bg.setPosition(this.renderer.width / 2, this.renderer.height / 2);
    this.bg.texture.setFilter(Phaser.Textures.FilterMode.NEAREST);

    this.title = this.add.text(
      this.renderer.width / 2,
      0,
      window.Config.localisation.main_menu.title,
      {
        fontFamily: "main_menu-font",
        fontSize: "50px",
        color: window.Config.skinFile.main_menu.text_color,
      }
    );
    this.title.setPosition(
      this.renderer.width / 2 - this.title.width / 2,
      -this.title.height
    );

    this.tweens.add({
      delay: 100,
      duration: 3000,
      targets: [this.title],
      y: 35 - this.title.height / 2,
      ease: "Bounce",
    });

    this.subtitle = this.add.text(
      10,
      100,
      window.Config.localisation.main_menu.subtitle_1,
      {
        fontFamily: "main_menu-font",
        fontSize: "15px",
        color: window.Config.skinFile.main_menu.text_color,
        wordWrap: {
          useAdvancedWrap: true,
          width: 300,
        },
      }
    );
    this.subtitle.setX(-this.subtitle.width);

    this.subtitle2 = this.add.text(
      0,
      100,
      window.Config.localisation.main_menu.subtitle_2,
      {
        fontFamily: "main_menu-font",
        fontSize: "15px",
        color: window.Config.skinFile.main_menu.text_color,
        wordWrap: {
          useAdvancedWrap: true,
          width: 300,
        },
      }
    );
    this.subtitle2.setX(this.renderer.width);

    this.tweens.add({
      delay: 2500,
      duration: 1000,
      targets: [this.subtitle],
      x: 10,
      ease: "Bounce",
    });

    this.tweens.add({
      delay: 2500,
      duration: 1000,
      targets: [this.subtitle2],
      x: this.renderer.width - this.subtitle2.width - 10,
      ease: "Bounce",
    });

    this.newGame = new TextButton(
      this,
      10,
      this.renderer.height / 4 + 75,
      window.Config.localisation.main_menu.buttons.new_game,
      {
        fontSize: "25px",
        fontFamily: "main_menu-font",
      },
      async () => {
        const dungeons = await API.getDungeons();
        const configs = await API.getConfigs();
        let dungeon: Dungeon;

        const result = await Swal.fire({
          title: window.Config.localisation.main_menu.buttons.new_game,
          heightAuto: false,
          html: `
          <label for="dungeon" class="f6 b db mb2 mt3"
              >Dungeon<span class="normal black-60"></span></label
          >
          <select
              id="dungeon"
              class="swal2-input"
              list="dungeons"
              required=""
              >
              ${dungeons.map((x) => `<option>${x}</option>`).join("\n")}
          </select>
          <label for="config" class="f6 b db mb2 mt3"
              >Configuration<span class="normal black-60"></span></label
          >
          <select
              id="config"
              class="swal2-input"
              list="configs"
              required=""
              >
              ${configs.map((x) => `<option>${x}</option>`).join("\n")}
          </select>
          `,
          focusConfirm: false,
          showCancelButton: true,
          showLoaderOnConfirm: true,
          allowOutsideClick: () => !Swal.isLoading(),
          preConfirm: async () => {
            let inputValidator = async (values) => {
              if (!values || !values[0]) {
                Swal.showValidationMessage("You need to select a dungeon");
                return false;
              }
              if (!values || !values[1]) {
                Swal.showValidationMessage("You need to select a config file");
                return false;
              }
              return values;
            };
            let results = [
              (document.getElementById("dungeon") as HTMLInputElement).value,
              (document.getElementById("config") as HTMLInputElement).value,
            ];
            if (await inputValidator(results)) {
              dungeon = await API.newGame(results[0], results[1]);
              return !!dungeon;
            }
          },
        });

        if (result.isConfirmed) {
          // To help students we won't do the *smart* thing here which is just
          // to invoke a load event on the game scene.  Instead we'll store the dungeon
          // in our window frame and then load the next scene to read from it.
          window.Dungeon = dungeon;
          this.scene.start("scene-game");
        }
      }
    );
    this.add.existing(this.newGame);
    this.newGame.setX(-this.newGame.width);

    this.exitGame = new TextButton(
      this,
      10,
      this.renderer.height / 4 +
        this.newGame.height +
        30 +
        20 +
        75,
      window.Config.localisation.main_menu.buttons.quit_game,
      {
        fontSize: "25px",
        fontFamily: "main_menu-font",
      },
      () => {
        this.game.destroy(true, true);
        InfoAlert(
          window.Config.localisation.main_menu.on_exit.title,
          window.Config.localisation.main_menu.on_exit.message
        );
      }
    );
    this.add.existing(this.exitGame);
    this.exitGame.setX(-this.exitGame.width);

    this.tweens.add({
      delay: 3500,
      duration: 1000,
      targets: [this.newGame, this.exitGame],
      x: 10,
      ease: "Bounce",
    });

    this.input.on("pointerup", () => {
      this.tweens.getAllTweens().forEach((x) => x.seek(1));
    });
  }

  update(time: number, delta: number) {}
}

export default MenuScene;
