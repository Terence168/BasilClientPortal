<template>
  <router-view />
</template>

<script>
import { defineComponent } from 'vue'
import "./themes/dark.css"
import { useUserStore } from './stores/user';
import { mapWritableState } from 'pinia';
import { Dark
 } from 'quasar';

export default defineComponent({
  name: 'App',
  computed:{
    ...mapWritableState(useUserStore, ["userDarkMode"]),
  },
  watch:{
    userDarkMode: function(){
      Dark.set(this.userDarkMode);
    }
  },
  created(){
    if(this.userDarkMode != "auto"){
      Dark.set(this.userDarkMode);
    }
  }
})
</script>
