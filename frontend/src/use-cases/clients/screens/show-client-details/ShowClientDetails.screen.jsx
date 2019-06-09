import React from 'react';

class ShowClientDetails extends React.Component {
    componentDidMount() {
        const { getClients, gammaLoadingFinished } = this.props;
        getClients().then(() => {
            gammaLoadingFinished();
        });
    }
    render() {
        const { client } = this.props;
    }
}