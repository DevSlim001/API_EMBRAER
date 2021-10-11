import api from "./../../services/api";

export default async function createCodelist(codelist,codManual){
    let data = {codelist:[...codelist]}
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.post(`/manual/${codManual}/codelist/create`,data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};