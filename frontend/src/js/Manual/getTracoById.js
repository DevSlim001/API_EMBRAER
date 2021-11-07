import api from "./../../services/api";

export default async function getTracoById(codTraco){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get(`/manual/traco/${codTraco}`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};