<template>
  <v-app>
    <v-main>
      <AList></AList>
    </v-main>
  </v-app>
</template>


<script>
import AList from "../components/ApplicationList";
export default {
  name: 'App',
  components: {
    AList,
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
