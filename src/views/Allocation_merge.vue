<template>
  <v-app>
    <v-main>
      <Groupmerge v-on="$listeners"></Groupmerge>
    </v-main>
  </v-app>
</template>


<script>
import Groupmerge from "../components/Group_merge";
export default {
  name: 'App',
  components: {
    Groupmerge,
  },

  data(){
    return{
      items:[]
    }
  },

  created(){
    this.$emit('login');
  },

  methods:{
    
  }

};
</script>
