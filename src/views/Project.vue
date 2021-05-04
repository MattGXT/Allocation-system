<template>
  <v-app>
    <v-main>
      <Projectlist v-on:numbers="setItem" v-on="$listeners"/>
        <v-container fluid v-if="role == 'student'">
            <v-layout justify-center>
               <v-flex xs12 sm12 md8>
                  <v-card>
                    <v-card-title>
                        Preferences
                    </v-card-title>
                    <v-col
                    cols="12"
                    sm="12"
                    >
                      <v-select
                        :items="items"
                        attach
                        chips
                        label="Select the project you want to join~"
                        multiple
                        v-on:input="limiter"
                      ></v-select>
                      <v-card-actions class="justify-center">
                        <v-btn color="primary">
                          Submit
                        </v-btn>
                      </v-card-actions>
                    </v-col>
                  </v-card>
               </v-flex>
            </v-layout>
         </v-container>
    </v-main>
  </v-app>
</template>

<script>
import Projectlist from '../components/Projectlist';
export default {
  name: 'App',
  components: {
    Projectlist,
  },

  data(){
    return{
      items:[],
      role:''
    }
  },

  created(){
    this.$emit('login');
    this.role = JSON.parse(localStorage.getItem("role"))
  },

  methods:{
    limiter(e) {
      if(e.length > 3) {
        console.log('you can only select two', e)
        e.pop()
      }
    },
    setItem(projects){
      projects.forEach(element => {
        this.items.push(element.id)
      });
    }
  }

};
</script>
