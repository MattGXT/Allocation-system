<template>
<div>
    <v-app>
      <v-content>
         <v-container fluid fill-height>
            <v-layout align-center justify-center>
               <v-flex xs12 sm8 md6>
                  <v-card>
                    <v-card-title>
                        Projects
                    <v-spacer></v-spacer>
                    <v-text-field
                        v-model="search"
                        append-icon="mdi-magnify"
                        label="Search"
                        single-line
                        hide-details
                    ></v-text-field>
                    </v-card-title>
                        <v-data-table
                        :headers="headers"
                        :items="Projects"
                        :search="search"
                            ></v-data-table>
                    </v-card>
               </v-flex>
            </v-layout>
         </v-container>
      </v-content>
   </v-app>
   </div>
</template>

<script>
  export default {
    data () {
      return {
        search: '',
        headers: [
          {
            text: 'Project',
            align: 'start',
            sortable: false,
            value: 'name',
          },
          { text: 'Description', value: 'description' },
        ],
        Projects:[
            {
                name:'The best project2',
                description:'Come on',
            },
            {
                name:'The best project',
                description:'Come on',
            },{
                name:'The best project',
                description:'Come on',
            }
        ],
      }
    },
  }
</script>