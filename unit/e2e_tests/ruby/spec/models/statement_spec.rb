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
    let(:api_instance) { UnitOpenapiRubySdk::UnitApi.new(UnitOpenapiRubySdk::ApiClient.new(configuration)) }


    it 'should get an instance of html statement' do
      opts = {
        query_params: {
          customer_id: '22603',
          language: 'en'
        }
      }
      response_data = api_instance.get_statement_html('146206', opts)

      # Read the content from the IO stream, if it's an IO object
      content = if response_data.is_a?(Tempfile) || response_data.is_a?(File)
                  File.read(response_data.path)
                else
                  response_data
                end

      # Check for common HTML tags in the content
      expect(content).to match(/<html[^>]*>/)
      expect(content).to include('<body>')
    end

    it 'should get an instance of pdf statement' do
      opts = { query_params: { customer_id: '22603' } }
      response_data = api_instance.get_statement_pdf('146206', opts)

      # Read the first few bytes of the Tempfile
      response_data.open
      initial_bytes = response_data.read(4)
      response_data.close

      expect(initial_bytes).to eq('%PDF')
    end

    it 'should get an instance of bank verification pdf statement' do
      response_data = api_instance.get_statement_bank_pdf('36981')

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