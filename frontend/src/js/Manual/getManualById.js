import api from "./../../services/api";

export default async function getManualById(codManual){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get(`/manual/${codManual}`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};