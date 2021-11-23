import { React, useState, useEffect } from 'react'
import { Button, Modal, Form, Spinner, Table, Row, Col } from 'react-bootstrap'
import $ from 'jquery';


import './index.css';
import getManuals from './../../js/Manual/getManuals'
import getManualById from './../../js/Manual/getManualById'
import getTracosByCodManual from './../../js/Manual/getTracosByCodManual';
import getRevisoesByCodManual from './../../js/Manual/getRevisoesByCodManual';
import downloadFull from './../../js/Manual/downloadFull';
import updateRevisoes from './../../js/Manual/updateRevisaoManual';

function Full() {

    const [manuais, setManuais] = useState([])
    const [revisoes, setRevisoes] = useState([])
    const [tracos, setTracos] = useState([])
    const [showFormRevisao, setShowFormRevisao] = useState(false)


    const loadManuals = () => {
        getManuals().then((res) => {
            setManuais(res.data)
        }).catch((err) => {
            console.log(err)
        })
    }

    const handleManualForm = (e) => {
        e.preventDefault()
        let form = $("#form-manual-full")
        let opt = form.serializeArray()[0].value
        let codManual = parseInt(opt)
        getManualById(codManual).then((res) => {
            getTracosByCodManual(codManual).then((res) => {
                setTracos(res.data)
                getRevisoesByCodManual(codManual).then((res) => {
                    setRevisoes(res.data)
                    setShowFormRevisao(true)
                }).catch(err => {
                    console.log(err)
                })
            }).catch(err => {
                console.log(err)
            })

        }).catch(err => {
            console.log(err)
        })
    }

    const handleUpdateRevisoes = (e) => {
        e.preventDefault()
        let form = $("#form-manual-full")
        let opt = form.serializeArray()[0].value
        let codManual = parseInt(opt)
        updateRevisoes(codManual).then((res) => {
            getRevisoesByCodManual(codManual).then((res) => {
                setRevisoes(res.data)
                setShowFormRevisao(true)
            }).catch(err => {
                console.log(err)
            })

        }).catch(err => {
            console.log(err)
        })
    }

    const handleDownloadFull = (e) => {
        e.preventDefault()
        let form = $("#form-download-full")
        let data = {}
        $.each(form.serializeArray(), function (i, field) {
            data[field.name] = field.value;

        });
        data.codManual = parseInt($("#form-manual-full").serializeArray()[0].value)
        downloadFull(data.codManual, data.codRevisao, data.traco).then(res => {
            console.log(res)
            let nomeFullFile = res.headers["content-disposition"].split("filename=\"")[1].split('"')[0]
            let fullBlob = res.data
            let urlFullFile = URL.createObjectURL(fullBlob)
            const downloadFull = window.document.createElement('a');
            downloadFull.href = urlFullFile;
            downloadFull.download = nomeFullFile
            downloadFull.click();
            window.URL.revokeObjectURL(downloadFull.href);

        }).catch(err => {
            console.log(err)
        })
    }

    useEffect(() => {
        loadManuals()
    }, [])


    return (
        <div id="form-div-full">
            <Form id="form-manual-full">
                <Form.Select aria-label="" defaultValue="" onInput={handleManualForm} name="manual" required>
                    <option value="" disabled >Selecione o manual</option>
                    {manuais.map((manual, index) => (
                        <option key={manual.codManual} value={manual.codManual}>{manual.nome}-{manual.partNumber}</option>
                    ))}
                </Form.Select>
            </Form>
            <br />
            {showFormRevisao &&
                <Form onSubmit={handleDownloadFull} id="form-download-full">
                   
                    <div className="d-grid gap-2">
                        <Button type="button" id="btn-att-revisoes" onClick={handleUpdateRevisoes} variant="primary">Atualizar registro das revisões</Button>
                    </div>
                    <br />
                    <Row>
                        <Col>
                            <Form.Group className="mb-3" controlId="revisao">
                                <Form.Label>Revisões</Form.Label>
                                <Form.Select aria-label="" defaultValue="" name="codRevisao" required>
                                    <option value="" disabled >Selecione a revisão</option>
                                    <option key='master' value='master'>Master</option>
                                    {revisoes.map((revisao, index) => (
                                        <option key={revisao.codRevisao} value={revisao.codRevisao}>{revisao.nomeRevisao}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>

                        </Col>
                        <Col>
                            <Form.Group className="mb-3" controlId="traco">
                                <Form.Label>Traços</Form.Label>
                                <Form.Select aria-label="" defaultValue="" name="traco" required>
                                    <option value="" disabled >Selecione o traço</option>
                                    {tracos.map((traco, index) => (
                                        <option key={traco.codTraco} value={traco.traco}>{traco.nome} - {traco.traco}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>
                    <div className="d-grid gap-2">
                        <Button type="submit" id="btn-download-full" variant="primary">Download</Button>
                    </div>
                </Form>
            }

        </div>
    );
}

export default Full;