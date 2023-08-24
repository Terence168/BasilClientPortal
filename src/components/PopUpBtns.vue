<template>
  <div class="popup-button-group" v-show="withClient" :style="positionStyle" ref="popup-button-group">
    <q-btn
      class="remove-unit"
      size="sm"
      color="red"
      icon="close"
      round
      v-show="showRemoveUnit"
      @click.prevent="this.$emit('popup-remove-sn')"
    />
    <q-btn
      class="edit-unit"
      size="sm"
      color="primary"
      icon="edit"
      round
      v-show="showUpdateUnit"
      @click.prevent="this.$emit('popup-update-sn')"
    />
    <q-btn
      class="view-unit"
      size="sm"
      color="yellow-5"
      icon="visibility"
      round
      v-show="showViewUnit"
      @click.prevent="this.$emit('popup-view-sn')"
    />
  </div>
</template>
<script>
export default {
  props: ["showViewUnit", "showUpdateUnit", "showRemoveUnit"],
  emits: ["popup-view-sn", "popup-update-sn", "popup-remove-sn"],
  computed: {
    count() {
      return (
        (this.showRemoveUnit ? 1 : 0) +
        (this.showUpdateUnit ? 1 : 0) +
        (this.showViewUnit ? 1 : 0)
      );
    },
  },
  mounted(){
    window.addEventListener('click', this.handleGlobalClick);
  },
  updated() {
    if (this.withClient === true && this.unitWidth == null) {
      let btn = null;
      if (this.showRemoveUnit === true) {
        btn = document.querySelector(".remove-unit");
      } else {
        btn = document.querySelector(".view-unit");
      }
      this.unitWidth = btn.getBoundingClientRect().width;

      const left = parseInt(this.positionStyle.left);
      const top = parseInt(this.positionStyle.top);

      const width =
        this.unitWidth * this.count + this.spaceInBtnGroup * (this.count - 1);

      this.positionStyle = {
        top: top - this.unitWidth / 2 + "px",
        left: left - width / 2 + "px",
        width: width + "px",
      };
      this.$forceUpdate();
    }
  },
  data() {
    return {
      withClient: false,
      mouseX: 0,
      mouseY: 0,
      positionStyle: { top: "0px", left: "0px" },
      spaceInBtnGroup: 6,
      timeOutId: null,
      unitWidth: null,
    };
  },
  methods: {
    addPopupBtns(event) {
      if (this.withClient === false) {
        this.withClient = true;
      }
      this.setPosition(event);

      //set 3000 timeout for popup buttons
      if (this.timeOutId != null) {
        //clear the old timeout and set a new one
        window.clearTimeout(this.timeOutId);
      }
      // this.window
      this.timeOutId = window.setTimeout(this.removePopupBtns, 3000);
      window.addEventListener("click", this.handleClickOutSerials);
    },
    setPosition(event) {
      this.mouseX = event.pageX;
      this.mouseY = event.pageY;

      if (this.unitWidth === null) {
        this.positionStyle = {
          top: this.mouseY + "px",
          left: this.mouseX + "px",
        };
      } else {
        const width =
        this.unitWidth * this.count + this.spaceInBtnGroup * (this.count - 1);
        this.positionStyle["top"] = this.mouseY - this.unitWidth / 2 + "px";
        this.positionStyle["left"] = 
          this.mouseX - (width/2) + "px";
      }
    },
    //listen to event
    removePopupBtns() {
      if (this.withClient === true) {
        this.withClient = false;
      }
    },
  },
};
</script>
<style>
.popup-button-group {
  position: absolute;
  height: max-content;
  justify-content: space-between;
  width: max-content;
  display: flex;
  flex-direction: row;

  .remove-unit {
    position: relative;
    z-index: 1200;
  }

  .edit-unit {
    position: relative;
    z-index: 1200;
  }
}
</style>
