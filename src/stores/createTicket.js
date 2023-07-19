import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

function compareAsc(p){
   return function(m,n){
      var a=m[p];
      var b=n[p];

      return a-b;
   }
}

function compareDesc(p){
   return function(m,n){
      var a=m[p];
      var b=n[p];

      return b-a;
   }
}

function selectMatchItem(lists,keyWord){

    let resArr=[];

    lists.filter(item=>{
       for(let i in item){
          if(item[i]!=null){
              if(item[i].toString().indexOf(keyWord)>=0){
                  resArr.push(item);
                  break;
              }
            }
       }
    })

    return resArr;
}


export const useCreateTicketStore = defineStore("createTicket", {
  state: () => ({
    page: 1,
    perPage:10,
    orderType: null,
    orderTypeOpt: null,
    order:false,
    trackingNums: [],
    serials: [],
    pageData:[],
    perPageData:[],
    inputValue:'',
  }),

  getters: {
    getSerials() {

     if(this.inputValue!='' && this.inputValue!=null){
        const searchData=selectMatchItem(this.serials,this.inputValue);

        this.page=1;
        this.perPage=10;

        const startIndex = (this.perPage * (this.page - 1));
        const endIndex = startIndex + this.perPage;

        return searchData.slice(startIndex, endIndex).reverse();


     }else{
          const startIndex = (this.perPage * (this.page - 1));
          const endIndex = startIndex + this.perPage;

          return this.serials.slice(startIndex,endIndex).reverse();

     }
    },
    getTotal(){
      return this.serials.length;
    },

    getTotalPages(){
      const numberOfPages = Math.ceil(this.serials.length/this.perPage);
      return numberOfPages;
    },
  },
  reset(){
     this.$refs.state.inputText.value='';
  },
  actions: {
    addTrackingNum() {
      this.trackingNums.push("");
    },

    deleteTrackingNum(index) {
      this.trackingNums.splice(index, 1);
    },
    populateOrderTypeOpt(_, update) {
      if (this.orderType) {
        update();
        return;
      }

      const link = "/ticketing/dropdown/repair_type";

      api
        .get(link)
        .then((response) => {
          update(() => {
            this.orderTypeOpt = response.data.data;
          });
        })
        .catch(function (error) {
          // handle error
          console.log(error);
          Notify.create({
            type: "negative",
            message: "Order Type Dropdown cannot be populated",
          });
        });
    },

    getNextPages(pages){
      this.page=pages;
    },

    changeToPage(perPage,pages){
       this.perPage=perPage;
    },

    goToPage(pages){
      this.page=pages;
    },

    sort(columnName){
      const startIndex =(this.perPage * (this.page - 1));
      const endIndex = startIndex + this.perPage;

      if(this.order==true){
         this.order=false;
         this.serials.sort(compareAsc(columnName)).reverse();;
      }else{
         this.order=true;
         this.serials.sort(compareDesc(columnName)).reverse();;
      }
    },

    addSerial(serialNumber, customerReportedIssue, terminalID) {
      const newSerial = {
        cosmetic: false,

        serialNumber: serialNumber,
        model: null,
        version: null,
        customerReportedIssue: customerReportedIssue,
        terminalID: terminalID,
        warrantyExpDate: null,
        warrantyStatus: null,
        customerRMA: null,

        repairPrice: null,

        show: true,
        loading: false,
      };

      this.serials.push(newSerial);
    },

    resetTicket() {
      this.$reset();
    },
  },

  persist: true,
});
