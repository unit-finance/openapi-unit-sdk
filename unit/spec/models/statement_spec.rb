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
    let(:pdf_statement) { OpenapiClient::GetStatementPDFApi.new(SpecHelper::TestApiClient.new(configuration)) }
    let(:verification_statement) { OpenapiClient::GetBankVerificationPDFApi.new(SpecHelper::TestApiClient.new(configuration)) }


    it 'should get an instance of html statement' do
      opts = {
        query_params: {
          customer_id: "22603",
          language: "en"
        }
      }
      _request, status_code, _headers = html_statement.execute_with_http_info("9755166", opts)

      expect(status_code).to eq(200)

    end

    it 'should get an instance of pdf statement' do
      opts = { query_params: { customer_id: "22603" } }
      _request, status_code, _headers = pdf_statement.execute_with_http_info("15554784", opts)
      expect(status_code).to eq(200)
      expect(_request).to include("PDF")
    end

    it 'should get an instance of bank verification pdf statement' do
      _request, status_code, _headers = verification_statement.execute_with_http_info("27573")
      expect(_request).to include("PDF")
      expect(status_code).to eq(200)
    end
  end
end
