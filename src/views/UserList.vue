<template>
  <v-app>
    <v-main>
      <User v-on="$listeners"/>
    </v-main>
  </v-app>
</template>

<script>
import User from '../components/User';
export default {
  name: 'App',
  components: {
    User,
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
