<template>
  <v-app>
    <v-main>
      <GroupList v-on="$listeners"/>
    </v-main>
  </v-app>
</template>


<script>
import GroupList from '../components/GroupList';
export default {
  name: 'App',
  components: {
    GroupList,
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
