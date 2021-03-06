import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';
import colors from 'vuetify/lib/util/colors'
import DatetimePicker from "vuetify-datetime-picker";

Vue.use(Vuetify);
Vue.use(DatetimePicker);

export default new Vuetify({
    theme: {
        themes: {
          light: {
            primary: colors.blue.lighten1, 
            secondary: colors.blue.darken1, 
            accent: colors.indigo.base,
            gray: "#808080",
            green:colors.green
          }
        },
      },
});
