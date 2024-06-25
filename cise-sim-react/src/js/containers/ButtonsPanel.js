/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Joint Research Centre (JRC) All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

import React, {Component} from 'react';
import {Grid} from '@material-ui/core';
import CreateMessageModal from "./CreateMessageModal";
import IncidentMessageModal from "./IncidentMessageModal";
import DiscoveryMessageModal from "./DiscoveryMessageModal";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import {observer} from "mobx-react";

// Set of utility/functional Buttons
@observer
export default class ButtonsPanel extends Component {


    render() {
        const showIncident = this.getServiceStore().serviceSelf.showIncident;
        const discoverySender = this.getServiceStore().serviceSelf.discoverySender;
        const discoveryServiceType = this.getServiceStore().serviceSelf.discoveryServiceType;
        const discoveryServiceOperation = this.getServiceStore().serviceSelf.discoveryServiceOperation;

        const doDiscovery = discoverySender && discoveryServiceType && discoveryServiceOperation;
        return (
            <TableContainer>
                <Table size="small">
                    <TableBody>
                        <TableRow>
                            <TableCell>
                                <Grid container alignItems="flex-end" justify="flex-end" direction="row">
                                    {doDiscovery ? <DiscoveryMessageModal store={this.props.store}
                                                                          sender={discoverySender}
                                                                          type={discoveryServiceType}
                                                                          operation={discoveryServiceOperation} /> : null}
                                    {showIncident ? <IncidentMessageModal store={this.props.store} /> : null }
                                    <CreateMessageModal store={this.props.store} />
                                </Grid>
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        )
    }
    getServiceStore() {
        return this.props.store.serviceStore;
    }
}
