import { React, useState, useEffect } from 'react'
import { Button, Modal, Form, Spinner, Table, Row, Col } from 'react-bootstrap'
import $ from 'jquery';


import './index.css';
import getManuals from './../../js/Manual/getManuals'
import getManualById from './../../js/Manual/getManualById'
import getTracosByCodManual from './../../js/Manual/getTracosByCodManual';
import getRevisoesByCodManual from './../../js/Manual/getRevisoesByCodManual';
import downloadDelta from './../../js/Manual/downloadDelta';

function Delta() {

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
        let form = $("#form-manual-delta")
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

    const handleDownloadDelta = (e) => {
        e.preventDefault()
        let form = $("#form-download-delta")
        let data = {}
        $.each(form.serializeArray(), function(i, field) {
            data[field.name] = parseInt(field.value);
        });
        data.codManual = parseInt($("#form-manual-delta").serializeArray()[0].value)
        downloadDelta(data.codManual,data.codRevisao,data.traco).then(res => {
            console.log(res)
            let nomeDeltaFile = res.headers["content-disposition"].split("filename=\"")[1].split('"')[0]
            let deltaBlob =res.data
            let urlDeltaFile = URL.createObjectURL(deltaBlob) 
            const downloadDelta = window.document.createElement('a');
            downloadDelta.href = urlDeltaFile;
            downloadDelta.download = nomeDeltaFile
            downloadDelta.click();
            window.URL.revokeObjectURL(downloadDelta.href);

        }).catch(err => {
            console.log(err)
        })
    }

    useEffect(() => {
        loadManuals()
    }, [])


    return (
        <div id="form-div-delta">
            <Form id="form-manual-delta">
                <Form.Select aria-label="" defaultValue="" onInput={handleManualForm} name="manual" required>
                    <option value="" disabled >Selecione o manual</option>
                    {manuais.map((manual, index) => (
                        <option key={manual.codManual} value={manual.codManual}>{manual.nome}-{manual.partNumber}</option>
                    ))}
                </Form.Select>
            </Form>
            <br />
            {showFormRevisao &&
                <Form onSubmit={handleDownloadDelta} id="form-download-delta">
                    <Row>
                        <Col>
                            <Form.Group className="mb-3" controlId="revisao">
                                <Form.Label>Revisões</Form.Label>
                                <Form.Select aria-label="" defaultValue="" name="codRevisao" required>
                                    <option value="" disabled >Selecione a revisão</option>
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
                        <Button type="submit" id="btn-download-delta" variant="primary">Download</Button>
                    </div>
                </Form>
            }


            {/* <Modal show={showModalCadastroManual} onHide={handleCloseModalCadastro}>
                <Modal.Header closeButton>
                    <Modal.Title>Cadastro de manual</Modal.Title>
                </Modal.Header>
                <Modal.Body id="modal-body">
                    <Form id="form-cadastro-manual">
                        <Form.Group className="mb-3" controlId="nome">
                            <Form.Label>Nome</Form.Label>
                            <Form.Control type="text" name="nome" placeholder="ABC" required />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="partNumber">
                            <Form.Label>Nome</Form.Label>
                            <Form.Control type="text" name="partNumber" placeholder="1234" required />
                        </Form.Group>
                    </Form>
                    <span id="msgModalCadastroManual">{msgModalCadastroManual}</span>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModalCadastro}>Voltar</Button>
                    <Button type="button" id="btn-cadastro-manual" variant="primary" onClick={handleCreateManual}>
                        {showSpinnerCadastroManual &&
                            <Spinner id="spinner-form-cadastro-manual" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                        }
                        Confirmar
                    </Button>
                </Modal.Footer>
            </Modal> */}
        </div>
    );
}

export default Delta;