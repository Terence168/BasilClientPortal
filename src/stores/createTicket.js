import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

/**
  filter input words
**/
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
    orderType: null,
    orderTypeOpt: null,
    trackingNums: [],
    serials: [],
    //pagination
    page: 1,
    perPage:10,
    inputValue:'',
    order:false,
    rangeFrom:0,
    rangeTo:0,
    searchData:[],
    iconSerialNumberPath:"/src/assets/asc.png",
    iconModelPath:"",
  }),

  getters: {
    getSerials() {

     this.rangeFrom = (this.page - 1) * this.perPage + 1;


     if(this.inputValue!='' && this.inputValue!=null){
        this.searchData=selectMatchItem(this.serials,this.inputValue);

        this.page=1;
        this.perPage=10;

        const startIndex = (this.perPage * (this.page - 1));
        const endIndex = startIndex + this.perPage;
        //when typing reload getTotal
        this.rangeTo = Math.min(this.page * this.perPage, this.getTotal);

        return this.searchData.slice(startIndex, endIndex);


     }else{
         const startIndex = (this.perPage * (this.page - 1));
         const endIndex = startIndex + this.perPage;

         this.rangeTo = Math.min(this.page * this.perPage, this.getTotal);
         return this.serials.slice(startIndex, endIndex);
     }


    },
    getTotal(){
     if(this.inputValue!='' && this.inputValue!=null){
        return this.searchData.length;
     }else{
        return this.serials.length;
      }
    },

    getTotalPages() {
      if (this.inputValue != "" && this.inputValue != null) {
        const numberOfPages = Math.ceil(this.searchData.length / this.perPage);
        return numberOfPages;
      } else {
        const numberOfPages = Math.ceil(this.serials.length / this.perPage);
        return numberOfPages;
      }
    },

    getRangeForm() {
      return this.rangeFrom;
    },

    getRangeTo() {
      return this.rangeTo;
    },

    getIconSerialNumberPath(){
       return this.iconSerialNumberPath;
    },

   getIconModelPath(){
       return this.iconModelPath;
    }
  },
  reset() {
    this.$refs.state.inputText.value = "";
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

    getNextPages(pages) {
      this.page = pages;
    },

    changeToPage(perPage, pages) {
      this.perPage = perPage;
    },

    goToPage(pages) {
      this.page = pages;
    },

    sort(columnName) {
      this.sortColumn = columnName;
      this.sortWithoutReverseOrder();
      this.order = !this.order;

      if(this.order === true){
        this.order = true;

        if(this.sortColumn=='serialNumber'){
          this.iconModelPath='';
          this.iconSerialNumberPath='/src/assets/desc.png';
        }

        if(this.sortColumn=="model"){
          this.iconModelPath='/src/assets/desc.png';
          this.iconSerialNumberPath='';
        }
      }else{
        this.order = false;

        if(this.sortColumn=='serialNumber'){
          this.iconModelPath='';
          this.iconSerialNumberPath='/src/assets/asc.png';
        }

       if(this.sortColumn=="model"){
          this.iconModelPath='/src/assets/asc.png';
          this.iconSerialNumberPath='';
       }


      }

    },

    sortWithoutReverseOrder() {
      const columnName = this.sortColumn;
      if (this.order === false) {
        this.serials.sort((s1, s2) =>
          s1[columnName] > s2[columnName]
            ? 1
            : s1[columnName] < s2[columnName]
            ? -1
            : 0
        );

      } else {
        this.serials.sort((s1, s2) =>
          s1[columnName] > s2[columnName]
            ? -1
            : s1[columnName] < s2[columnName]
            ? 1
            : 0
        );


      }
    },

    addSerial(serialData) {
      const newSerial = {...serialData, cosmetic:false, show:true, loading:false };
      if(newSerial.serialNumber === "00000000"){
        console.log("null serial number input");
        return;
      }
      if (this.isSerialNumberUnqiue(newSerial.serialNumber) === false) {
          return;
      }
      this.serials.unshift(newSerial);
    },

    updateSerial(serialData, oldSerialNumber) {
      const { serialNumber} = serialData;
      if (
        serialNumber != oldSerialNumber &&
        this.isSerialNumberUnqiue(serialNumber) === false
      ) {
        return;
      }
      const newSerial = {...serialData, cosmetic:false, show:true, loading:false };
      const index = this.serials.findIndex(
        (s) => oldSerialNumber === s.serialNumber
      );
      this.serials[index] = newSerial;
    },

    resetTicket() {
      this.$reset();
    },

    removeSerial(serialNumber) {
      const index = this.serials.findIndex(
        (s) => serialNumber === s.serialNumber
      );
      if (index >= 0) {
        this.serials.splice(index, 1);
      }
    },

    isSerialNumberUnqiue(serialNumber) {
      const index = this.serials.findIndex(
        (s) => serialNumber === s.serialNumber
      );
      if (index != -1) {
        console.log("serial Number is duplicate " + serialNumber);
        Notify.create({
          type: "negative",
          message: "Serial Numbers are not Unique",
        });
        return false;
      }
      return true;
    },
  },

  persist: true,
});
