# frozen_string_literal: true

require 'spec_helper'
require 'json'
require 'date'

RSpec.describe 'Statement' do
  before do
    # run before each test
    configure_tests
  end

  describe 'test an instance of Statement' do
    let(:html_statement) { OpenapiClient::GetStatementHTMLApi.new(OpenapiClient::ApiClient.new(configuration)) }
    let(:pdf_statement) { OpenapiClient::GetStatementPDFApi.new(OpenapiClient::ApiClient.new(configuration)) }
    let(:verification_statement) do
      OpenapiClient::GetBankVerificationPDFApi.new(OpenapiClient::ApiClient.new(configuration))
    end

    it 'should get an instance of html statement' do
      opts = {
        query_params: {
          customer_id: '22603',
          language: 'en'
        }
      }
      _request, status_code, _headers = html_statement.execute_with_http_info('9755166', opts)

      expect(status_code).to eq(200)
    end

    it 'should get an instance of pdf statement' do
      opts = { query_params: { customer_id: '22603' } }
      response_data, status_code, headers = pdf_statement.execute_with_http_info('15554784', opts)

      expect(status_code).to eq(200)
      expect(headers['Content-Type']).to include('application/pdf')

      # Read the first few bytes of the Tempfile
      response_data.open
      initial_bytes = response_data.read(4)
      response_data.close

      expect(initial_bytes).to eq('%PDF')
    end



    it 'should get an instance of bank verification pdf statement' do
      response_data, status_code, headers = verification_statement.execute_with_http_info('27573')
      expect(status_code).to eq(200)

      # If the response_data is a string:
      if response_data.is_a?(String)
        expect(response_data[0, 4]).to eq('%PDF')
      elsif response_data.is_a?(Tempfile)  # If the response_data is a Tempfile:
        response_data.open
        initial_bytes = response_data.read(4)
        response_data.close
        expect(initial_bytes).to eq('%PDF')
      else
        fail "Unexpected response_data type: #{response_data.class}"
      end
    end
  end
end
